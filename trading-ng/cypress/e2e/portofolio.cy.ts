import { HeaderPo } from '../support/po/header.po';
import { LoginPo } from '../support/po/login.po';
import { PortofolioPo } from '../support/po/portofolio.po';

describe('Portofolio', () => {

  it('User without assets', () => {
    cy.visit('/');
    HeaderPo.login().click();

    LoginPo.loginField().type('user');
    LoginPo.passwordField().type('password');
    LoginPo.loginButton().should('be.enabled').click();

    PortofolioPo.summary().should('be.visible').click();
    PortofolioPo.assets().should('not.exist');
  });

  it('User with assets', () => {
    cy.visit('/');
    HeaderPo.login().click();

    LoginPo.loginField().type('admin');
    LoginPo.passwordField().type('password');
    LoginPo.loginButton().should('be.enabled').click();

    PortofolioPo.summary().should('contain.text', '€17,291.48').click();
    PortofolioPo.assets().should('have.length', 4).first().should('contain.text', 'British Pound Sterling');

    PortofolioPo.searchIcon().should('be.visible');
    PortofolioPo.searchClear().should('not.exist');
    PortofolioPo.searchBar().type('Ubisoft');

    PortofolioPo.assets().should('have.length', 1).should('contain.text', 'Ubisoft Entertainment SA (UBI)');

    PortofolioPo.searchIcon().should('not.exist');
    PortofolioPo.searchClear().should('be.visible').click();
    PortofolioPo.searchBar().should('be.empty');

    PortofolioPo.assets().should('have.length', 4).first().should('contain.text', 'British Pound Sterling');
  });
});
