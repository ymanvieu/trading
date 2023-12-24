import { defineConfig } from "cypress";

export default defineConfig({
  hosts: {
    localhost: '127.0.0.1'
  },
  e2e: {
    baseUrl: "http://localhost:4201",
    viewportWidth: 1280,
    viewportHeight: 720,
    setupNodeEvents(on, config) {
      require("cypress-fail-fast/plugin")(on, config);

      config.env.FAIL_FAST_STRATEGY = "spec";

      return config;
    },
  },

  component: {
    viewportWidth: 700,
    viewportHeight: 700,
    devServer: {
      framework: "angular",
      bundler: "webpack",
    },
    specPattern: "**/*.cy.ts",
  },
});
