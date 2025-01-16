/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.commands

import org.jetbrains.plugins.ideavim.VimTestCase
import org.junit.jupiter.api.Test

/**
 * @author Alex Plate
 */
class ExitCommandTest : VimTestCase() {
  @Test
  fun `test single file`() {
    setupChecks {
      neoVim.exitOnTearDown = false
    }
    @Suppress("IdeaVimAssertState")
    val psiFile = fixture.configureByText("A_Discovery", "Lorem ipsum dolor sit amet,")
    fileManager.openFile(psiFile.virtualFile, false)
    kotlin.test.assertNotNull<Any>(fileManager.currentFile)

    typeText(commandToKeys("qa"))
    kotlin.test.assertNull(fileManager.currentFile)
  }

  @Test
  fun `test full command`() {
    setupChecks {
      neoVim.exitOnTearDown = false
    }
    @Suppress("IdeaVimAssertState")
    val psiFile = fixture.configureByText("A_Discovery", "Lorem ipsum dolor sit amet,")
    fileManager.openFile(psiFile.virtualFile, false)
    kotlin.test.assertNotNull<Any>(fileManager.currentFile)

    typeText(commandToKeys("qall"))
    kotlin.test.assertNull(fileManager.currentFile)
  }

  @Suppress("IdeaVimAssertState")
  @Test
  fun `test multiple files`() {
    setupChecks {
      neoVim.exitOnTearDown = false
    }
    val psiFile1 = fixture.configureByText("A_Discovery1", "Lorem ipsum dolor sit amet,")
    val psiFile2 = fixture.configureByText("A_Discovery2", "consectetur adipiscing elit")
    fileManager.openFile(psiFile1.virtualFile, false)
    fileManager.openFile(psiFile2.virtualFile, false)
    kotlin.test.assertNotNull<Any>(fileManager.currentFile)

    typeText(commandToKeys("qa"))
    kotlin.test.assertNull(fileManager.currentFile)
  }
}
