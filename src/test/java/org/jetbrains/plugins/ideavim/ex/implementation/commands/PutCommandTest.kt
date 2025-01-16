/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.commands

import com.maddyhome.idea.vim.api.injector
import org.jetbrains.plugins.ideavim.VimTestCase
import org.junit.jupiter.api.Test

/**
 * @author Alex Plate
 */
class PutCommandTest : VimTestCase() {
  // VIM-550 |:put|
  @Test
  fun `test put creates new line`() {
    configureByText("Test\n" + "Hello <caret>World!\n")
    typeText(injector.parser.parseKeys("\"ayw"))
    typeText(commandToKeys("put a"))
    assertState(
      "Test\n" +
        "Hello World!\n" +
        "<caret>World\n",
    )
  }

  // VIM-551 |:put|
  @Test
  fun `test put default`() {
    configureByText("<caret>Hello World!\n")
    typeText(injector.parser.parseKeys("yw"))
    typeText(commandToKeys("put"))
    assertState("Hello World!\n" + "<caret>Hello \n")
  }
}
