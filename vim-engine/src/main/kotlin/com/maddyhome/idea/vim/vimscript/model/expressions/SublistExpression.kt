/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.vimscript.model.expressions

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.ex.ExException
import com.maddyhome.idea.vim.vimscript.model.VimLContext
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimDataType
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimDictionary
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString

data class SublistExpression(val from: Expression?, val to: Expression?, val expression: Expression) : Expression() {

  override fun evaluate(editor: VimEditor, context: ExecutionContext, vimContext: VimLContext): VimDataType {
    val expressionValue = expression.evaluate(editor, context, vimContext)
    val arraySize = when (expressionValue) {
      is VimDictionary -> throw ExException("E719: Cannot slice a Dictionary")
      is VimList -> expressionValue.values.size
      else -> expressionValue.asString().length
    }
    var fromInt = Integer.parseInt(from?.evaluate(editor, context, vimContext)?.asString() ?: "0")
    if (fromInt < 0) {
      fromInt += arraySize
    }
    var toInt = Integer.parseInt(to?.evaluate(editor, context, vimContext)?.asString() ?: (arraySize - 1).toString())
    if (toInt < 0) {
      toInt += arraySize
    }
    return if (expressionValue is VimList) {
      if (fromInt > arraySize) {
        VimList(mutableListOf())
      } else if (fromInt == toInt) {
        expressionValue.values[fromInt]
      } else if (fromInt <= toInt) {
        VimList(expressionValue.values.subList(fromInt, toInt + 1))
      } else {
        VimList(mutableListOf())
      }
    } else {
      if (fromInt > arraySize) {
        VimString("")
      } else if (fromInt <= toInt) {
        if (toInt > expressionValue.asString().length - 1) {
          VimString(expressionValue.asString().substring(fromInt))
        } else {
          VimString(expressionValue.asString().substring(fromInt, toInt + 1))
        }
      } else {
        VimString("")
      }
    }
  }
}
