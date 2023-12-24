import { HeaderPo } from '../support/po/header.po';
import { LoginPo } from '../support/po/login.po';
import { PortofolioPo } from '../support/po/portofolio.po';

describe('Login - local user', () => {

  it('Login page', () => {

    cy.visit('/');
    HeaderPo.login().click();

    LoginPo.google().should('be.visible');
    LoginPo.github().should('be.visible');

    LoginPo.loginButton().should('be.disabled');

    LoginPo.loginField().type('some-user');
    LoginPo.passwordField().type('some-password');

    LoginPo.loginButton().should('be.enabled');
  });

  it('Login as USER', () => {
    cy.visit('/');
    HeaderPo.login().click();

    LoginPo.loginField().type('user');
    LoginPo.passwordField().type('password');

    LoginPo.loginButton().should('be.enabled').click();

    HeaderPo.portofolio().should('be.visible');
    HeaderPo.admin().should('not.exist');
    HeaderPo.login().should('not.exist');
    HeaderPo.signup().should('not.exist');

    HeaderPo.currencyQuantity().should('contain.text', '€100,000.00');

    PortofolioPo.summary().should('be.visible').click();

    PortofolioPo.assets().should('not.exist');
  });

  it('Login as ADMIN', () => {
    cy.visit('/');
    HeaderPo.login().click();

    LoginPo.loginField().type('admin');
    LoginPo.passwordField().type('password');
    LoginPo.loginButton().should('be.enabled').click();

    HeaderPo.portofolio().should('be.visible');
    HeaderPo.admin().should('be.visible');

    HeaderPo.currencyQuantity().should('contain.text', '€18,500.00');

    PortofolioPo.summary().should('be.visible').click();
  });
});
