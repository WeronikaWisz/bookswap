import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileComponent } from "./views/manage-users/profile/profile.component";
import { RegisterComponent } from "./views/manage-users/register/register.component";
import { LoginComponent } from "./views/manage-users/login/login.component";
import {BrowseBooksComponent} from "./views/user-books/browse-books/browse-books.component";
import {AddBookComponent} from "./views/user-books/add-book/add-book.component";
import {BrowseOffersComponent} from "./views/book-offers/browse-offers/browse-offers.component";
import {BrowseSwapRequestsComponent} from "./views/book-offers/browse-swap-requests/browse-swap-requests.component";

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'my-books', component: BrowseBooksComponent },
  { path: 'add-book', component: AddBookComponent },
  { path: 'edit-book/:id', component: AddBookComponent },
  { path: 'browse-offers', component: BrowseOffersComponent },
  { path: 'browse-offers/:username/:label', component: BrowseOffersComponent },
  { path: 'swap-requests/:direction', component: BrowseSwapRequestsComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
