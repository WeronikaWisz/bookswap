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

@Component({
  selector: 'app-filters-dialog',
  templateUrl: './filters-dialog.component.html',
  styleUrls: ['./filters-dialog.component.sass'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS],
    },

    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
  ]
})
export class FiltersDialogComponent implements OnInit {

  separatorKeysCodes: number[] = [ENTER, COMMA];

  categoriesCtrl = new FormControl();
  filteredCategories: Observable<string[]>;
  categories: string[] = [];
  allCategories: string[] = ['Przygodowa', 'Obyczajowa'];

  titlesCtrl = new FormControl();
  filteredTitles: Observable<string[]>;
  titles: string[] = [];
  allTitles: string[] = ['Tytul 1', 'Tytul 2'];

  authorsCtrl = new FormControl();
  filteredAuthors: Observable<string[]>;
  authors: string[] = [];
  allAuthors: string[] = ['Autor 1', 'Autor 2'];

  publishersCtrl = new FormControl();
  filteredPublishers: Observable<string[]>;
  publishers: string[] = [];
  allPublishers: string[] = ['Wydawnictwo 1', 'Wydawnictwo 2'];

  dateFrom : FormControl;
  dateTo : FormControl;

  maxDate: Date;

  exampleHeaderFrom = DataPickerHeader;
  exampleHeaderTo = DataPickerHeader;

  @ViewChild('categoryInput') categoryInput!: ElementRef<HTMLInputElement>;
  @ViewChild('titleInput') titleInput!: ElementRef<HTMLInputElement>;
  @ViewChild('authorInput') authorInput!: ElementRef<HTMLInputElement>;
  @ViewChild('publisherInput') publisherInput!: ElementRef<HTMLInputElement>;

  constructor(
    public dialogRef: MatDialogRef<FiltersDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BookFilter,
  ) {

    this.filteredCategories = this.categoriesCtrl.valueChanges.pipe(
      startWith(null),
      map((category: string | null) => (category ? this._filter(category, 'category') : this.allCategories.slice())),
    );
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

    const currentYear = moment();
    this.maxDate = new Date(currentYear.year(), 11, 31);

    this.categories = data.categories ? data.categories : []
    this.titles = data.titles ? data.titles : []
    this.authors = data.authors ? data.authors : []
    this.publishers = data.publishers ? data.publishers : []

    console.log(data)
    if (data.yearOfPublicationFrom) {
      this.dateFrom = new FormControl(moment('31/12/'+data.yearOfPublicationFrom, "DD/MM/YYYY"));
    } else {
      this.dateFrom = new FormControl();
    }
    if (data.yearOfPublicationTo) {
      this.dateTo = new FormControl(moment('31/12/'+data.yearOfPublicationTo, "DD/MM/YYYY"));
    } else {
      this.dateTo = new FormControl();
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
    this.data.yearOfPublicationFrom = this.dateFrom.value?.year();
    this.data.yearOfPublicationTo = this.dateTo.value?.year();
    this.data.categories = this.categories;
    return this.data
  }

}
