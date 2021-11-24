import {
  Component,
  OnInit,
  Inject,
  ViewChild,
  ElementRef,
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { BookFilter } from "../../../../models/user-books/BookFilter";
import {FormControl} from "@angular/forms";
import {Observable} from "rxjs";
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {map, startWith} from 'rxjs/operators';
import {MatChipInputEvent} from "@angular/material/chips";
import {MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS} from '@angular/material-moment-adapter';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {MatDatepicker} from '@angular/material/datepicker';

import * as _moment from 'moment';
import {default as _rollupMoment, Moment} from 'moment';

const moment = _rollupMoment || _moment;

import {MY_FORMATS} from "../../../../helpers/data-picker.header";
import {DataPickerHeader} from "../../../../helpers/data-picker.header";
import {FilterDialogData} from "../../../../models/book-offers/FilterDialogData";

@Component({
  selector: 'app-filter-offers-dialog',
  templateUrl: './filter-offers-dialog.component.html',
  styleUrls: ['./filter-offers-dialog.component.sass'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS],
    },

    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
  ]
})
export class FilterOffersDialogComponent implements OnInit {

  separatorKeysCodes: number[] = [ENTER, COMMA];

  categoriesCtrl = new FormControl();
  filteredCategories: Observable<string[]>;
  categories: string[] = [];
  allCategories: string[] = [];

  titlesCtrl = new FormControl();
  filteredTitles: Observable<string[]>;
  titles: string[] = [];
  allTitles: string[] = [];

  authorsCtrl = new FormControl();
  filteredAuthors: Observable<string[]>;
  authors: string[] = [];
  allAuthors: string[] = [];

  publishersCtrl = new FormControl();
  filteredPublishers: Observable<string[]>;
  publishers: string[] = [];
  allPublishers: string[] = [];

  localizationCtrl = new FormControl();
  filteredLocalization: Observable<string[]>;
  localization: string[] = [];
  allLocalization: string[] = [];

  ownersCtrl = new FormControl();
  filteredOwners: Observable<string[]>;
  owners: string[] = [];
  allOwners: string[] = [];

  dateFrom : FormControl;
  dateTo : FormControl;

  maxDate: Date;

  exampleHeaderFrom = DataPickerHeader;
  exampleHeaderTo = DataPickerHeader;

  @ViewChild('categoryInput') categoryInput!: ElementRef<HTMLInputElement>;
  @ViewChild('titleInput') titleInput!: ElementRef<HTMLInputElement>;
  @ViewChild('authorInput') authorInput!: ElementRef<HTMLInputElement>;
  @ViewChild('publisherInput') publisherInput!: ElementRef<HTMLInputElement>;
  @ViewChild('localizationInput') localizationInput!: ElementRef<HTMLInputElement>;
  @ViewChild('ownerInput') ownerInput!: ElementRef<HTMLInputElement>;

  constructor(
    public dialogRef: MatDialogRef<FilterOffersDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FilterDialogData,
  ) {
    dialogRef.disableClose = true;
    this.filteredTitles = this.titlesCtrl.valueChanges.pipe(
      startWith(null),
      map((title: string | null) => (title ? this._filter(title, 'title') : this.allTitles.slice())),
    );
    this.filteredAuthors = this.authorsCtrl.valueChanges.pipe(
      startWith(null),
      map((author: string | null) => (author ? this._filter(author, 'author') : this.allAuthors.slice())),
    );
    this.filteredPublishers = this.publishersCtrl.valueChanges.pipe(
      startWith(null),
      map((publisher: string | null) => (publisher ? this._filter(publisher, 'publisher') : this.allPublishers.slice())),
    );
    this.filteredLocalization = this.localizationCtrl.valueChanges.pipe(
      startWith(null),
      map((loc: string | null) => (loc ? this._filter(loc, 'localization') : this.allLocalization.slice())),
    );
    this.filteredOwners = this.ownersCtrl.valueChanges.pipe(
      startWith(null),
      map((owner: string | null) => (owner ? this._filter(owner, 'owner') : this.allOwners.slice())),
    );
    this.filteredCategories = this.categoriesCtrl.valueChanges.pipe(
      startWith(null),
      map((category: string | null) => (category ? this._filter(category, 'category') : this.allCategories.slice())),
    );

    const currentYear = moment();
    this.maxDate = new Date(currentYear.year(), 11, 31);

    this.titles = data.bookFilter.titles ? data.bookFilter.titles : [];
    this.authors = data.bookFilter.authors ? data.bookFilter.authors : [];
    this.publishers = data.bookFilter.publishers ? data.bookFilter.publishers : [];
    this.localization = data.bookFilter.localization ? data.bookFilter.localization : [];
    this.owners = data.bookFilter.owners ? data.bookFilter.owners : [];
    this.categories = data.bookFilter.categories ? data.bookFilter.categories : [];

    console.log(data)
    if (data.bookFilter.yearOfPublicationFrom) {
      this.dateFrom = new FormControl(moment('31/12/'+data.bookFilter.yearOfPublicationFrom, "DD/MM/YYYY"));
    } else {
      this.dateFrom = new FormControl();
    }
    if (data.bookFilter.yearOfPublicationTo) {
      this.dateTo = new FormControl(moment('31/12/'+data.bookFilter.yearOfPublicationTo, "DD/MM/YYYY"));
    } else {
      this.dateTo = new FormControl();
    }

    this.setHints()

  }

  setHints(){
    if(this.data.filterHints){
      this.allCategories = this.data.filterHints.categories;
      this.allTitles = this.data.filterHints.titles;
      this.allAuthors = this.data.filterHints.authors;
      this.allPublishers = this.data.filterHints.publishers;
      this.allLocalization = this.data.filterHints.localization;
      this.allOwners = this.data.filterHints.owners;
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
  }

  add(event: MatChipInputEvent, name: string): void {
    const value = (event.value || '').trim();
    if (value) {
      if(name === 'category') {
        if(!this.categories.find(c => c === value)) {
          this.categories.push(value);
        }
      } else if(name === 'title') {
        if(!this.titles.find(t => t === value)) {
          this.titles.push(value);
        }
      } else if(name === 'author') {
        if(!this.authors.find(a => a === value)) {
          this.authors.push(value);
        }
      } else if(name === 'publisher') {
        if(!this.publishers.find(p => p === value)) {
          this.publishers.push(value);
        }
      } else if(name === 'localization') {
        if (!this.localization.find(p => p === value)) {
          this.localization.push(value);
        }
      } else if(name === 'owner') {
        if (!this.owners.find(p => p === value)) {
          this.owners.push(value);
        }
      }
    }
    event.chipInput!.clear();
    if(name === 'category') {
      this.categoriesCtrl.setValue(null);
    } else if(name === 'title') {
      this.titlesCtrl.setValue(null);
    } else if(name === 'author') {
      this.authorsCtrl.setValue(null);
    } else if(name === 'publisher') {
      this.publishersCtrl.setValue(null);
    } else if(name === 'localization') {
      this.localizationCtrl.setValue(null);
    } else if(name === 'owner') {
      this.ownersCtrl.setValue(null);
    }
  }

  remove(item: string, name: string): void {
    if(name === 'category') {
      const index = this.categories.indexOf(item);
      if (index >= 0) {
        this.categories.splice(index, 1);
      }
    } else if(name === 'title') {
      const index = this.titles.indexOf(item);
      if (index >= 0) {
        this.titles.splice(index, 1);
      }
    } else if(name === 'author') {
      const index = this.authors.indexOf(item);
      if (index >= 0) {
        this.authors.splice(index, 1);
      }
    } else if(name === 'publisher') {
      const index = this.publishers.indexOf(item);
      if (index >= 0) {
        this.publishers.splice(index, 1);
      }
    } else if(name === 'localization') {
      const index = this.localization.indexOf(item);
      if (index >= 0) {
        this.localization.splice(index, 1);
      }
    } else if(name === 'owner') {
      const index = this.owners.indexOf(item);
      if (index >= 0) {
        this.owners.splice(index, 1);
      }
    }
  }

  selected(event: MatAutocompleteSelectedEvent, name: string): void {
    if(name === 'category') {
      if(!this.categories.find(c => c === event.option.viewValue)) {
        this.categories.push(event.option.viewValue);
      }
      this.categoryInput.nativeElement.value = '';
      this.categoriesCtrl.setValue(null);
    } else if(name === 'title') {
      if(!this.titles.find(t => t === event.option.viewValue)) {
        this.titles.push(event.option.viewValue);
      }
      this.titleInput.nativeElement.value = '';
      this.titlesCtrl.setValue(null);
    } else if(name === 'author') {
      if(!this.authors.find(a => a === event.option.viewValue)) {
        this.authors.push(event.option.viewValue);
      }
      this.authorInput.nativeElement.value = '';
      this.authorsCtrl.setValue(null);
    } else if(name === 'publisher') {
      if(!this.publishers.find(p => p === event.option.viewValue)) {
        this.publishers.push(event.option.viewValue);
      }
      this.publisherInput.nativeElement.value = '';
      this.publishersCtrl.setValue(null);
    } else if(name === 'localization') {
      if(!this.localization.find(l => l === event.option.viewValue)) {
        this.localization.push(event.option.viewValue);
      }
      this.localizationInput.nativeElement.value = '';
      this.localizationCtrl.setValue(null);
    } else if(name === 'owner') {
      if(!this.owners.find(o => o === event.option.viewValue)) {
        this.owners.push(event.option.viewValue);
      }
      this.ownerInput.nativeElement.value = '';
      this.ownersCtrl.setValue(null);
    }
  }

  private _filter(value: string, name: string): string[] {
    if(name === 'category') {
      const filterValue = value.toLowerCase();
      return this.allCategories.filter(category => category.toLowerCase().includes(filterValue));
    } else if(name === 'title') {
      const filterValue = value.toLowerCase();
      return this.allTitles.filter(title => title.toLowerCase().includes(filterValue));
    } else if(name === 'author') {
      const filterValue = value.toLowerCase();
      return this.allAuthors.filter(author => author.toLowerCase().includes(filterValue));
    } else if(name === 'publisher') {
      const filterValue = value.toLowerCase();
      return this.allPublishers.filter(publisher => publisher.toLowerCase().includes(filterValue));
    } else if(name === 'localization') {
      const filterValue = value.toLowerCase();
      return this.allLocalization.filter(localization => localization.toLowerCase().includes(filterValue));
    } else if(name === 'owner') {
      const filterValue = value.toLowerCase();
      return this.allOwners.filter(owner => owner.toLowerCase().includes(filterValue));
    } else {
      return [];
    }
  }

  chosenYearHandler(normalizedYear: Moment, datepicker: MatDatepicker<Moment>, mode: string) {
    if( mode === 'from'){
      this.dateFrom.setValue(moment());
    } else {
      this.dateTo.setValue(moment());
    }
    const ctrlValue = mode === 'from' ? this.dateFrom.value : this.dateTo.value;
    ctrlValue.year(normalizedYear.year());
    if( mode === 'from'){
      this.dateFrom.setValue(ctrlValue);
    } else {
      this.dateTo.setValue(ctrlValue);
    }
    datepicker.close();
    if(this.dateTo.value && this.dateFrom.value && this.dateTo.value.year() < this.dateFrom.value.year()){
      this.dateTo.setErrors({'incorrect': true});
    } else {
      this.dateTo.setErrors(null);
    }
  }

  sendData(): BookFilter {
    this.data.bookFilter.yearOfPublicationFrom = this.dateFrom.value?.year();
    this.data.bookFilter.yearOfPublicationTo = this.dateTo.value?.year();
    return this.data.bookFilter
  }

}
