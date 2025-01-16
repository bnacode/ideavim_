/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */
package com.maddyhome.idea.vim.action.motion.leftright

import com.intellij.vim.annotations.CommandOrMotion
import com.intellij.vim.annotations.Mode
import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.ImmutableVimCaret
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.api.options
import com.maddyhome.idea.vim.command.Argument
import com.maddyhome.idea.vim.command.MotionType
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.handler.Motion
import com.maddyhome.idea.vim.handler.MotionActionHandler

@CommandOrMotion(keys = ["<BS>", "<C-H>"], modes = [Mode.NORMAL, Mode.VISUAL])
open class MotionBackspaceAction(private val allowPastEnd: Boolean = false) : MotionActionHandler.ForEachCaret() {
  override fun getOffset(
    editor: VimEditor,
    caret: ImmutableVimCaret,
    context: ExecutionContext,
    argument: Argument?,
    operatorArguments: OperatorArguments,
  ): Motion {
    val allowWrap = injector.options(editor).whichwrap.contains("b")
    return injector.motion.getHorizontalMotion(editor, caret, -operatorArguments.count1, allowPastEnd, allowWrap)
  }

  override val motionType: MotionType = MotionType.EXCLUSIVE
}

// When the motion is used with an operator, the EOL character is counted.
// This allows e.g., `d<BS>` to delete the end of line character on the previous line when wrap is active
// ('whichwrap' contains "b")
// See `:help whichwrap`. This says a delete or change operator, but it appears to apply to all operators
@CommandOrMotion(keys = ["<BS>", "<C-H>"], modes = [Mode.OP_PENDING])
class MotionBackspaceOpPendingModeAction : MotionBackspaceAction(allowPastEnd = true)