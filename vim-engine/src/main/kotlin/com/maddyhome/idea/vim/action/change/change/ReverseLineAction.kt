package com.maddyhome.idea.vim.action.change.change

import com.intellij.vim.annotations.CommandOrMotion
import com.intellij.vim.annotations.Mode
import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimCaret
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector

import com.maddyhome.idea.vim.command.Command
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.handler.VimActionHandler

@CommandOrMotion(keys = ["\\r"], modes = [Mode.NORMAL])

class ReverseLineAction : VimActionHandler.ForEachCaret() {

  override val type: Command.Type = Command.Type.CHANGE

  override fun execute(
    editor: VimEditor,
    caret: VimCaret,
    context: ExecutionContext,
    cmd: Command,
    operatorArguments: OperatorArguments,
  ): Boolean {

    // Step 1: Get the current line's text
    val lineNum = caret.getLine()
    val currentLineText = editor.getLineText(lineNum)

    // Step 2: Reverse the text
    val reversedText = currentLineText.reversed()

    val originalOffset = caret.offset

    // Step 3: Replace the line with reversed text
    injector.changeGroup.deleteLine(editor, context, caret, 1, operatorArguments)
    injector.changeGroup.insertText(editor, caret, reversedText + "\n")

    caret.moveToOffset(originalOffset)

    return true // Indicate successful execution
  }
}
