/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.api

/**
 * This is terrible, just unbind VimPlugin from IJ
 * THis class is created only to move some other classes to vim-engine
 */
interface VimEnabler {
  fun isEnabled(): Boolean
}
