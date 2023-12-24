import { AdminPo } from '../support/po/admin.po';
import { HeaderPo } from '../support/po/header.po';
import { LoginPo } from '../support/po/login.po';

describe('Administration', () => {

  it('result before search', () => {
    cy.visit('/');
    HeaderPo.login().click();

    LoginPo.loginField().type('admin');
    LoginPo.passwordField().type('password');
    LoginPo.loginButton().click();
    HeaderPo.admin().click();

    AdminPo.lines().first().should('contain.text', 'Ubisoft Entertainment SA');
  });
});
