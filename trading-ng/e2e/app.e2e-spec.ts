import { TotoPage } from './app.po';

describe('toto App', () => {
  let page: TotoPage;

  beforeEach(() => {
    page = new TotoPage();
  });

  it('should display welcome message', done => {
    page.navigateTo();
    page.getParagraphText()
      .then(msg => expect(msg).toEqual('Welcome to app!!'))
      .then(done, done.fail);
  });
});
