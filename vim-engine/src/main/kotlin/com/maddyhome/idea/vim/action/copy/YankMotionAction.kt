/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */
package com.maddyhome.idea.vim.action.copy

import com.intellij.vim.annotations.CommandOrMotion
import com.intellij.vim.annotations.Mode
import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.Argument
import com.maddyhome.idea.vim.command.Command
import com.maddyhome.idea.vim.command.DuplicableOperatorAction
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.handler.VimActionHandler

@CommandOrMotion(keys = ["y"], modes = [Mode.NORMAL])
class YankMotionAction : VimActionHandler.SingleExecution(), DuplicableOperatorAction {
  override val type: Command.Type = Command.Type.COPY

  override val argumentType: Argument.Type = Argument.Type.MOTION

  override val duplicateWith: Char = 'y'

  override fun execute(
    editor: VimEditor,
    context: ExecutionContext,
    cmd: Command,
    operatorArguments: OperatorArguments,
  ): Boolean {
    val argument = cmd.argument ?: return false
    return injector.yank.yankMotion(editor, context, argument, operatorArguments)
  }
}
