import 'jasmine';
import { AppPo } from '../support/po/app.po';
import { HeaderPo } from '../support/po/header.po';
import { LatestListPo } from '../support/po/latest-list.po';
import { LoginPo } from '../support/po/login.po';
import { PortofolioPo } from '../support/po/portofolio.po';

describe('Can login as local standard user', () => {

  it('can login', () => {
    AppPo.navigateToHomePage();
    HeaderPo.loginLink().click();

    LoginPo.page().should('be.visible');

    LoginPo.loginButton().should('be.disabled');

    LoginPo.loginField().type('user');
    LoginPo.passwordField().type('password');

    LoginPo.loginButton().should('be.enabled').click();

    PortofolioPo.page().should('be.visible');
    PortofolioPo.summary().should('be.visible').click();
  });
});
