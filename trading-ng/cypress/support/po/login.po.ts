export class LoginPo {

  private static page = 'app-login';

  static google() {
    return cy.get(`${this.page} [data-test="google-login"]`);
  }

  static github() {
    return cy.get(`${this.page} [data-test="github-login"]`);
  }

  static loginField() {
    return cy.get(`${this.page} [data-test="username"]`);
  }

  static passwordField() {
    return cy.get(`${this.page} [data-test="password"]`);
  }

  static loginButton() {
    return cy.get(`${this.page} [data-test="login"]`);
  }
}
