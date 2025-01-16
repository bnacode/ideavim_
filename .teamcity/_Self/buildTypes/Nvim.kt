package _Self.buildTypes

import _Self.Constants.NVIM_TESTS
import _Self.IdeaVimBuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.CheckoutMode
import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object Nvim : IdeaVimBuildType({
  name = "Tests with nvim"
  description = "Running tests with nvim integration"

  params {
    param("env.ORG_GRADLE_PROJECT_downloadIdeaSources", "false")
    param("env.ORG_GRADLE_PROJECT_ideaVersion", NVIM_TESTS)
    param("env.ORG_GRADLE_PROJECT_instrumentPluginCode", "false")
    param("env.ideavim.nvim.path", "./nvim-linux64/bin/nvim")
  }

  vcs {
    root(DslContext.settingsRoot)
    branchFilter = "+:<default>"

    checkoutMode = CheckoutMode.AUTO
  }

  steps {
    script {
      name = "Set up NeoVim"
      scriptContent = """
              wget https://github.com/neovim/neovim/releases/download/v0.7.2/nvim-linux64.tar.gz
              tar xzf nvim-linux64.tar.gz
              cd nvim-linux64/bin
              chmod +x nvim
              """.trimIndent()
    }
    gradle {
      tasks = "clean test -Dnvim"
      buildFile = ""
      enableStacktrace = true
    }
  }

  triggers {
    vcs {
      branchFilter = "+:<default>"
    }
  }

  failureConditions {
    failOnMetricChange {
      metric = BuildFailureOnMetric.MetricType.TEST_COUNT
      threshold = 20
      units = BuildFailureOnMetric.MetricUnit.PERCENTS
      comparison = BuildFailureOnMetric.MetricComparison.LESS
      compareTo = build {
        buildRule = lastSuccessful()
      }
    }
  }
})
