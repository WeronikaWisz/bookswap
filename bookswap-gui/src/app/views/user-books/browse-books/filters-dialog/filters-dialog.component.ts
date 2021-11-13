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

  dateFrom : FormControl;
  dateTo : FormControl;

  maxDate: Date;

  exampleHeaderFrom = DataPickerHeader;
  exampleHeaderTo = DataPickerHeader;

  @ViewChild('categoryInput') categoryInput!: ElementRef<HTMLInputElement>;

  constructor(
    public dialogRef: MatDialogRef<FiltersDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BookFilter,
  ) {
    this.filteredCategories = this.categoriesCtrl.valueChanges.pipe(
      startWith(null),
      map((category: string | null) => (category ? this._filter(category) : this.allCategories.slice())),
    );
    const currentYear = moment();
    this.maxDate = new Date(currentYear.year(), 11, 31);
    this.categories = data.categories ? data.categories : []
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

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    if (value) {
      this.categories.push(value);
    }

    event.chipInput!.clear();

    this.categoriesCtrl.setValue(null);
  }

  remove(category: string): void {
    const index = this.categories.indexOf(category);

    if (index >= 0) {
      this.categories.splice(index, 1);
    }
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    this.categories.push(event.option.viewValue);
    this.categoryInput.nativeElement.value = '';
    this.categoriesCtrl.setValue(null);
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.allCategories.filter(category => category.toLowerCase().includes(filterValue));
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
