import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

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
import { ReceivedOffersComponent } from './views/book-offers/received-offers/received-offers.component';
import { SentOffersComponent } from './views/book-offers/sent-offers/sent-offers.component';
import { OfferDetailsDialogComponent } from './views/book-offers/browse-offers/offer-details-dialog/offer-details-dialog.component';
import { FilterOffersDialogComponent } from './views/book-offers/browse-offers/filter-offers-dialog/filter-offers-dialog.component';

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
    ReceivedOffersComponent,
    SentOffersComponent,
    OfferDetailsDialogComponent,
    FilterOffersDialogComponent
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
        MatCheckboxModule
    ],
  providers: [authInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
