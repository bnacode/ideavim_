/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.parser.expressions

import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import com.maddyhome.idea.vim.vimscript.parser.VimscriptParser
import org.jetbrains.plugins.ideavim.ex.evaluate
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringParsingTests {

  @Test
  fun `quoted string`() {
    assertEquals(
      VimString("oh, hi Mark"),
      VimscriptParser.parseExpression("\"oh, hi Mark\"")!!.evaluate(),
    )
  }

  @Test
  fun `single quoted string`() {
    assertEquals(
      VimString("oh, hi Mark"),
      VimscriptParser.parseExpression("'oh, hi Mark'")!!.evaluate(),
    )
  }

  @Test
  fun `escaped backslash in quoted string`() {
    assertEquals(
      VimString("oh, \\hi Mark"),
      VimscriptParser.parseExpression("\"oh, \\\\hi Mark\"")!!.evaluate(),
    )
  }

  @Test
  fun `escaped quote quoted string`() {
    assertEquals(
      VimString("oh, hi \"Mark\""),
      VimscriptParser.parseExpression("\"oh, hi \\\"Mark\\\"\"")!!.evaluate(),
    )
  }

  @Test
  fun `backslashes in single quoted string`() {
    assertEquals(
      VimString("oh, hi \\\\Mark\\"),
      VimscriptParser.parseExpression("'oh, hi \\\\Mark\\'")!!.evaluate(),
    )
  }

  @Test
  fun `escaped single quote in single quoted string`() {
    assertEquals(
      VimString("oh, hi 'Mark'"),
      VimscriptParser.parseExpression("'oh, hi ''Mark'''")!!.evaluate(),
    )
  }

  @Test
  fun `single quoted string inside a double quoted string`() {
    assertEquals(
      VimString(" :echo \"no mapping for 45\"<CR>"),
      VimscriptParser.parseExpression(
        """
         ' :echo "no mapping for ' . 45 . '"<CR>'
        """.trimIndent(),
      )!!.evaluate(),
    )
  }
}
