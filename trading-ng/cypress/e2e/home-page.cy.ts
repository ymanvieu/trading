import 'jasmine';
import { AppPo } from '../support/po/app.po';
import { HeaderPo } from '../support/po/header.po';
import { LatestListPo } from '../support/po/latest-list.po';

describe('Home page', () => {

  it('should display home page', () => {
    AppPo.navigateToHomePage();

    HeaderPo.loginLink().should('be.visible').contains('Log in');

    LatestListPo.datagrid().should('be.visible');
  });
});
