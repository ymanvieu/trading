export class LoginPo {

  static page() {
    return cy.get('app-login');
  }

  static loginField() {
    return this.page().find('input[formcontrolname="username"]');
  }

  static passwordField() {
    return this.page().find('input[formcontrolname="password"]');
  }

  static loginButton() {
    return this.page().contains('button', 'Log in');
  }
}
