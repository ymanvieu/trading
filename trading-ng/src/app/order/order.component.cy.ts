import { HttpClient, HttpClientModule } from '@angular/common/http';
import { OrderPo } from '../../../cypress/support/po/order.po';
import { Asset } from '../portofolio/model/asset';
import { Symbol } from '../symbol/model/symbol';
import { OrderComponent } from './order.component';

describe('order.component.cy.ts', () => {

  let availableSymbols: Symbol[];
  let asset: Asset;

  before(() => {
    cy.fixture<Symbol[]>('portofolio/available-symbols.json').then(aS => availableSymbols = aS);
    cy.fixture<Asset>('portofolio/asset.json').then(a => asset = a);
  });

  it('Buy new asset', () => {

    cy.mount(OrderComponent, {
      autoSpyOutputs: true,
      componentProperties: {
        availableSymbols: availableSymbols
      }
    });

    cy.intercept('GET', '/api/portofolio/order-info*', {fixture: '/portofolio/order-info_buy.json'}).as('orderInfo');

    cy.intercept('POST', '/api/portofolio/order', {}).as('order');

    OrderPo.modal().should('not.exist');

    OrderPo.buyNewAsset().should('have.text', 'Buy new asset').click();

    OrderPo.ModalPo.symbolSelector().should('be.visible');

    OrderPo.ModalPo.increment().click();

    OrderPo.ModalPo.selectedQuantity().should('have.value', 2);

    OrderPo.ModalPo.decrement().click();

    OrderPo.ModalPo.selectedQuantity().should('have.value', 1);

    OrderPo.ModalPo.decrement().click();

    OrderPo.ModalPo.selectedQuantity().should('have.value', 1);

    OrderPo.ModalPo.confirmOrder().should('have.text', 'BUY').click();

    cy.get('@orderCompletedSpy').should('have.been.calledOnce');

    OrderPo.modal().should('not.exist');

    ////////////

    OrderPo.buyNewAsset().click();

    cy.intercept('POST', '/api/portofolio/order', {statusCode: 400, fixture: '/portofolio/order/order_not-enough-fund.json'}).as('order');

    OrderPo.ModalPo.confirmOrder().click();

    OrderPo.ModalPo.errorMessage().should('have.text', 'Not enough fund for buying: 1 TTWO (owned: 100 USD, needed: 155.6 USD)');
  });


  it('Buy asset', () => {

    cy.mount(OrderComponent, {
      autoSpyOutputs: true,
      componentProperties: {
        asset: asset
      }
    });

    cy.intercept('GET', '/api/portofolio/order-info*', {fixture: '/portofolio/order-info_buy.json'}).as('orderInfo');

    cy.intercept('POST', '/api/portofolio/order', {}).as('order');

    OrderPo.modal().should('not.exist');

    OrderPo.buy().should('have.text', 'Buy').click();

    OrderPo.ModalPo.symbolSelector().should('not.exist');
  });


  it('Sell asset', () => {

    cy.mount(OrderComponent, {
      autoSpyOutputs: true,
      componentProperties: {
        asset: asset
      }
    });

    cy.intercept('GET', '/api/portofolio/order-info*', {fixture: '/portofolio/order-info_sell.json'}).as('orderInfo');

    cy.intercept('POST', '/api/portofolio/order', {}).as('order');

    OrderPo.modal().should('not.exist');

    OrderPo.sell().should('have.text', 'Sell').click();

    OrderPo.ModalPo.symbolSelector().should('not.exist');
    OrderPo.ModalPo.sellAll().should('be.visible');

    OrderPo.ModalPo.increment().click();

    OrderPo.ModalPo.selectedQuantity().should('have.value', 2);

    OrderPo.ModalPo.decrement().click();

    OrderPo.ModalPo.selectedQuantity().should('have.value', 1);

    OrderPo.ModalPo.decrement().click();

    OrderPo.ModalPo.selectedQuantity().should('have.value', 1);

    OrderPo.ModalPo.confirmOrder().should('have.text', 'SELL').click();

    cy.get('@orderCompletedSpy').should('have.been.calledOnce');

    OrderPo.modal().should('not.exist');

    ////////////

    OrderPo.sell().click();

    cy.intercept('POST', '/api/portofolio/order', {statusCode: 400, fixture: '/portofolio/order/order_not-enough-owned.json'}).as('order');

    OrderPo.ModalPo.confirmOrder().click();

    OrderPo.ModalPo.errorMessage().should('have.text', 'Not enough owned for selling: 6 TTWO (owned: 5)');

    ////////////

    cy.intercept('POST', '/api/portofolio/order', {}).as('order');

    OrderPo.ModalPo.sellAll().click();

    OrderPo.ModalPo.selectedQuantity().should('have.value', 5);

    OrderPo.ModalPo.confirmOrder().click();

    cy.get('@orderCompletedSpy').should('have.been.calledTwice');
  });
});
