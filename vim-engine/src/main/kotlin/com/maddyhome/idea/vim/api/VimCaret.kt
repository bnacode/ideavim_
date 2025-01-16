/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.api

import com.maddyhome.idea.vim.common.LiveRange
import com.maddyhome.idea.vim.common.TextRange
import com.maddyhome.idea.vim.group.visual.VisualChange
import com.maddyhome.idea.vim.group.visual.vimMoveBlockSelectionToOffset
import com.maddyhome.idea.vim.group.visual.vimMoveSelectionToCaret
import com.maddyhome.idea.vim.handler.Motion
import com.maddyhome.idea.vim.helper.RWLockLabel
import com.maddyhome.idea.vim.helper.StrictMode
import com.maddyhome.idea.vim.helper.exitVisualMode
import com.maddyhome.idea.vim.register.Register
import com.maddyhome.idea.vim.state.mode.SelectionType
import com.maddyhome.idea.vim.state.mode.inBlockSelection
import com.maddyhome.idea.vim.state.mode.inCommandLineMode
import com.maddyhome.idea.vim.state.mode.inSelectMode
import com.maddyhome.idea.vim.state.mode.inVisualMode
import javax.swing.KeyStroke

/**
 * Immutable interface of the caret. Immutable caret is an important concept of Fleet.
 * This interface is not yet actively adapted around vim-engine.
 *
 * I'll be needed to understand how can we merge mutable IJ carets and fully immutable Fleet carets.
 * This interface exposes immutable public sealedctions of the caret, but doesn't solve this issue completely.
 *
 * TODO: Switch names: ImmutableVimCaret -> VimCaret & VimCaret -> MutableVimCaret
 *       to be consistent with VimEditor
 */
interface ImmutableVimCaret {
  val editor: VimEditor
  val offset: Int
  val isValid: Boolean
  val isPrimary: Boolean

  @RWLockLabel.Readonly
  val selectionStart: Int

  @RWLockLabel.Readonly
  val selectionEnd: Int
  val vimSelectionStart: Int

  val vimLastColumn: Int

  fun getBufferPosition(): BufferPosition

  // TODO: [visual] Try to remove this. Visual position is an IntelliJ concept and Vim doesn't have a direct equivalent
  fun getVisualPosition(): VimVisualPosition

  fun getLine(): Int

  /**
   * Return the buffer line of the caret as a 1-based public value, as used by VimScript
   */
  val vimLine: Int
  val visualLineStart: Int
  fun hasSelection(): Boolean

  var lastSelectionInfo: SelectionInfo
  val registerStorage: CaretRegisterStorage
  val markStorage: LocalMarkStorage
}

interface VimCaret : ImmutableVimCaret {
  override var vimLastColumn: Int
  fun resetLastColumn()

  /*
This variable should not exist. This is actually `< mark in visual selection. It should be refactored as we'll get
per-caret marks.
*/
  override var vimSelectionStart: Int

  fun vimSelectionStartClear()

  fun setSelection(start: Int, end: Int)
  fun removeSelection()

  fun moveToOffset(offset: Int): VimCaret {
    if (offset < 0 || offset > editor.text().length || !isValid) return this
    if (editor.inBlockSelection) {
      StrictMode.assert(this == editor.primaryCaret(), "Block selection can only be moved with primary caret!")

      // Note that this call replaces ALL carets, so any local caret instances will be invalid!
      vimMoveBlockSelectionToOffset(editor, offset)
      injector.scroll.scrollCaretIntoView(editor)
      return this
    }

    // Make sure to always reposition the caret, even if the offset hasn't changed. We might need to reposition due to
    // changes in surrounding text, especially with inline inlays.
    val oldOffset = this.offset
    var caretAfterMove = moveToInlayAwareOffset(offset)

    // Similarly, always make sure the caret is positioned within the view. Adding or removing text could move the caret
    // position relative to the view, without changing offset.
    if (this == editor.primaryCaret()) {
      injector.scroll.scrollCaretIntoView(editor)
    }
    caretAfterMove = if (editor.inVisualMode || editor.inSelectMode) {
      // Another inconsistency with immutable caret. This method should be called on the new caret instance.
      caretAfterMove.vimMoveSelectionToCaret(this.vimSelectionStart)
      editor.findLastVersionOfCaret(caretAfterMove) ?: caretAfterMove
    } else if (editor.inCommandLineMode && caretAfterMove.hasSelection()) {
      // If we're updating the caret in Command-line mode, it's most likely due to incsearch
      caretAfterMove.setSelection(caretAfterMove.selectionStart, offset)
      editor.findLastVersionOfCaret(caretAfterMove) ?: caretAfterMove
    } else {
      editor.exitVisualMode()
      caretAfterMove
    }
    injector.motion.onAppCodeMovement(editor, this, offset, oldOffset)
    return caretAfterMove
  }

  fun moveToOffsetNative(offset: Int)

  /**
   * We return here an instance of the caret because the caret implementation may be immutable
   */
  fun moveToInlayAwareOffset(newOffset: Int): VimCaret
  fun moveToBufferPosition(position: BufferPosition)

  // TODO: [visual] Try to remove this. Visual position is an IntelliJ concept and Vim doesn't have a direct equivalent
  fun moveToVisualPosition(position: VimVisualPosition)

  /**
   * Same as setter for [vimLastColumn] but returns the new version of the caret.
   * As the common strategies for caret processing are not yet created, there is no need to adapt
   *   this method around IdeaVim right now
   */
  fun setVimLastColumnAndGetCaret(col: Int): VimCaret

  var vimInsertStart: LiveRange
  var vimLastVisualOperatorRange: VisualChange?
}

fun VimCaret.moveToMotion(motion: Motion): VimCaret {
  return if (motion is Motion.AbsoluteOffset) {
    this.moveToOffset(motion.offset)
  } else {
    // todo what should we do if it is a error motion?
    this
  }
}

interface CaretRegisterStorage {
  val caret: ImmutableVimCaret

  /**
   * Stores text to caret's recordable (named/numbered/unnamed) register
   */
  @Deprecated("Please use the same method, but with ExecutionContext")
  fun storeText(editor: VimEditor, range: TextRange, type: SelectionType, isDelete: Boolean): Boolean
  fun storeText(
    editor: VimEditor,
    context: ExecutionContext,
    range: TextRange,
    type: SelectionType,
    isDelete: Boolean,
  ): Boolean

  /**
   * Gets text from caret's recordable register
   * If the register is not recordable - global text state will be returned
   */
  @Deprecated("Please use com.maddyhome.idea.vim.api.CaretRegisterStorage#getRegister(com.maddyhome.idea.vim.api.VimEditor, com.maddyhome.idea.vim.api.ExecutionContext, char)")
  fun getRegister(r: Char): Register?
  fun getRegister(editor: VimEditor, context: ExecutionContext, r: Char): Register?

  @Deprecated("Please use com.maddyhome.idea.vim.api.CaretRegisterStorage#setKeys(com.maddyhome.idea.vim.api.VimEditor, com.maddyhome.idea.vim.api.ExecutionContext, char, java.util.List<? extends javax.swing.KeyStroke>)")
  fun setKeys(register: Char, keys: List<KeyStroke>)
  fun setKeys(editor: VimEditor, context: ExecutionContext, register: Char, keys: List<KeyStroke>)

  @Deprecated("Please use com.maddyhome.idea.vim.api.CaretRegisterStorage#saveRegister(com.maddyhome.idea.vim.api.VimEditor, com.maddyhome.idea.vim.api.ExecutionContext, char, com.maddyhome.idea.vim.register.Register)")
  fun saveRegister(r: Char, register: Register)
  fun saveRegister(editor: VimEditor, context: ExecutionContext, r: Char, register: Register)
}