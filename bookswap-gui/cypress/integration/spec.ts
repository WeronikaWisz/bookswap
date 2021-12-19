describe('ManageUserModule', () => {
  it('should redirect to login page', () => {
    cy.visit('/')
    cy.url().should('includes', 'login')
  })
  it('should have disabled login button', () => {
    cy.get('button.mat-raised-button')
      .should('be.disabled')
  })
})
