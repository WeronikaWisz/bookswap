describe('Test sign in', () => {
  it('should redirect to login page', () => {
    cy.visit('/')
    cy.url().should('includes', 'login')
  })
  it('should have disabled login button', () => {
    cy.get('button.mat-raised-button')
      .should('be.disabled')
  })
  it('should login', () => {
    cy.fixture('login-data').then((data) => {
      const {username, password} = data
      cy.get('input[formcontrolname="username"]').type(username)
      cy.get('input[formcontrolname="password"]').type(password)
      cy.get('button.mat-raised-button')
        .should('not.be.disabled')
      cy.get('button.mat-raised-button').click()
      cy.url().should('include', 'browse-offers')
    })
  })
  it('should save token in sessionStorage', () => {
    cy.window().its("sessionStorage")
      .invoke("getItem", "auth-token")
      .should("exist")
  })
})
describe('Test add book', function () {
  it('should have disabled add button', () => {
    cy.visit('/add-book')
    cy.get('button.mat-raised-button')
      .should('be.disabled')
  })
  it('should add book', () => {
    cy.fixture('book-data').then((data) => {
      const {title, author, publisher, yearOfPublication, categories} = data
      cy.get('input[formcontrolname="title"]').type(title)
      cy.get('input[formcontrolname="author"]').type(author)
      cy.get('input[formcontrolname="publisher"]').type(publisher)
      cy.get('input[formcontrolname="yearOfPublication"]').type(yearOfPublication)
      cy.get('mat-chip-list').children().type(categories+'{enter}')
      cy.get('mat-select[formcontrolname="label"]').click().get('mat-option').first().click()
      cy.get('button.mat-raised-button')
        .should('not.be.disabled')
      cy.get('button.mat-raised-button').click()
    })
  })
  it('should disabled add button after add', () => {
    cy.get('button.mat-raised-button')
      .should('be.disabled')
  })
});
describe('Test send request', () => {
  it('should send request', () => {
    cy.visit('/browse-offers')
    cy.get('.offer-list').children().children().first().click()
    cy.get('div.mat-dialog-content > .book-basic > .swap-button > form > button').click()
  })
  it('should cancel request', () => {
    cy.visit('/swap-requests/sent')
    cy.get('.offer-list').children().children().first()
      .get('mat-card-actions > button').click()
  })
})
