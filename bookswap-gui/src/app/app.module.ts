import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MainNavComponent } from './main-nav/main-nav.component';
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from "@angular/material/menu";
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatCardModule} from "@angular/material/card";
import {MatSelectModule} from "@angular/material/select";
import { LoginComponent } from './views/manage-users/login/login.component';
import { RegisterComponent } from './views/manage-users/register/register.component';
import { ProfileComponent } from './views/manage-users/profile/profile.component';

import { authInterceptorProviders } from './helpers/auth.interceptor';
import {MatStepperModule} from "@angular/material/stepper";
import {FlexLayoutModule} from "@angular/flex-layout";
import { BrowseBooksComponent } from './views/user-books/browse-books/browse-books.component';
import {MatRippleModule} from "@angular/material/core";
import {FiltersDialogComponent} from './views/user-books/browse-books/filters-dialog/filters-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import {MatChipsModule} from "@angular/material/chips";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatDatepickerModule} from "@angular/material/datepicker";
import { AddBookComponent } from './views/user-books/add-book/add-book.component';
import {DataPickerHeader} from "./helpers/data-picker.header";
import { BookDetailsDialogComponent } from './views/user-books/browse-books/book-details-dialog/book-details-dialog.component';
import {MatTabsModule} from "@angular/material/tabs";
import {MatCheckboxModule} from "@angular/material/checkbox";
import { BrowseOffersComponent } from './views/book-offers/browse-offers/browse-offers.component';
import { BrowseSwapRequestsComponent } from './views/book-offers/browse-swap-requests/browse-swap-requests.component';
import { OfferDetailsDialogComponent } from './views/book-offers/browse-offers/offer-details-dialog/offer-details-dialog.component';
import { FilterOffersDialogComponent } from './views/book-offers/browse-offers/filter-offers-dialog/filter-offers-dialog.component';
import {MatTooltipModule} from "@angular/material/tooltip";
import { UpdateProfileDialogComponent } from './views/manage-users/profile/update-profile-dialog/update-profile-dialog.component';
import { ChangePasswordDialogComponent } from './views/manage-users/profile/change-password-dialog/change-password-dialog.component';
import { BrowseSwapsComponent } from './views/book-swaps/browse-swaps/browse-swaps.component';
import { UserAddressDialogComponent } from './views/book-swaps/browse-swaps/user-address-dialog/user-address-dialog.component';

import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {MatPaginatorModule} from "@angular/material/paginator";

@NgModule({
  declarations: [
    AppComponent,
    MainNavComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    BrowseBooksComponent,
    FiltersDialogComponent,
    DataPickerHeader,
    AddBookComponent,
    BookDetailsDialogComponent,
    BrowseOffersComponent,
    BrowseSwapRequestsComponent,
    OfferDetailsDialogComponent,
    FilterOffersDialogComponent,
    UpdateProfileDialogComponent,
    ChangePasswordDialogComponent,
    BrowseSwapsComponent,
    UserAddressDialogComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        LayoutModule,
        MatToolbarModule,
        MatButtonModule,
        MatSidenavModule,
        MatIconModule,
        MatListModule,
        MatMenuModule,
        FormsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatSelectModule,
        MatInputModule,
        MatFormFieldModule,
        MatStepperModule,
        FlexLayoutModule,
        MatRippleModule,
        MatDialogModule,
        MatChipsModule,
        MatAutocompleteModule,
        MatDatepickerModule,
        MatTabsModule,
        MatCheckboxModule,
        MatTooltipModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        MatPaginatorModule
    ],
  providers: [authInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http);
}
