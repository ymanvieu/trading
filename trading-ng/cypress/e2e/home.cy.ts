import { HeaderPo } from '../support/po/header.po';
import { LatestListPo } from '../support/po/latest-list.po';

describe('Home page', () => {

  it('should display home page', () => {
    cy.visit('/');

    HeaderPo.home().should('be.visible').contains('Trading');
    HeaderPo.login().should('be.visible').contains('Log in');
    HeaderPo.signup().should('be.visible').contains('Sign up');

    LatestListPo.table().should('be.visible');
    LatestListPo.favorites().should('not.exist');
    LatestListPo.lines().first().should('contain.text', 'Bitcoin').should('contain.text', '$16,949.00');
  });
});
