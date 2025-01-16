/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */
package com.maddyhome.idea.vim.action.change.delete

import com.intellij.vim.annotations.CommandOrMotion
import com.intellij.vim.annotations.Mode
import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimCaret
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.Argument
import com.maddyhome.idea.vim.command.Command
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.handler.ChangeEditorActionHandler

@CommandOrMotion(keys = ["<Del>"], modes = [Mode.NORMAL])
class DeleteCharacterAction : DeleteCharacter({ 1 })

@CommandOrMotion(keys = ["X"], modes = [Mode.NORMAL])
class DeleteCharacterLeftAction : DeleteCharacter({ -it })

@CommandOrMotion(keys = ["x"], modes = [Mode.NORMAL])
class DeleteCharacterRightAction : DeleteCharacter({ it })

abstract class DeleteCharacter(private val countModifier: (Int) -> Int) : ChangeEditorActionHandler.ForEachCaret() {
  override val type: Command.Type = Command.Type.DELETE

  override fun execute(
    editor: VimEditor,
    caret: VimCaret,
    context: ExecutionContext,
    argument: Argument?,
    operatorArguments: OperatorArguments,
  ): Boolean {
    return injector.changeGroup.deleteCharacter(
      editor,
      context,
      caret,
      countModifier(operatorArguments.count1),
      false,
      operatorArguments,
    )
  }
}
