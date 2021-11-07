import {
  Component,
  OnInit,
  Inject,
  ViewChild,
  ElementRef,
  ChangeDetectionStrategy,
  OnDestroy,
  ChangeDetectorRef
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { BookFilter } from "../../models/user-books/BookFilter";
import {FormControl, Validators} from "@angular/forms";
import {Observable, Subject} from "rxjs";
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {map, startWith, takeUntil} from 'rxjs/operators';
import {MatChipInputEvent} from "@angular/material/chips";
import {MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS} from '@angular/material-moment-adapter';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatDateFormats} from '@angular/material/core';
import {MatCalendar, MatDatepicker} from '@angular/material/datepicker';

import * as _moment from 'moment';
import {default as _rollupMoment, Moment} from 'moment';

const moment = _rollupMoment || _moment;

export const MY_FORMATS = {
  parse: {
    dateInput: 'YYYY',
  },
  display: {
    dateInput: 'YYYY'
  },
};


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

  exampleHeaderFrom = ExampleHeader;
  exampleHeaderTo = ExampleHeader;

  @ViewChild('fruitInput') fruitInput!: ElementRef<HTMLInputElement>;

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

  remove(fruit: string): void {
    const index = this.categories.indexOf(fruit);

    if (index >= 0) {
      this.categories.splice(index, 1);
    }
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    this.categories.push(event.option.viewValue);
    this.fruitInput.nativeElement.value = '';
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

@Component({
  selector: 'example-header',
  styles: [
    `
    .example-header
      display: flex
      align-items: center
      padding: 0.5em

    .example-header-label
      flex: 1
      height: 1em
      font-weight: 500
      text-align: center

    .example-double-arrow .mat-icon
      margin: -22%

  `,
  ],
  template: `
    <div class="example-header">
      <button mat-icon-button (click)="previousClicked()">
        <mat-icon>keyboard_arrow_left</mat-icon>
      </button>
      <span class="example-header-label">{{periodLabel}}</span>
      <button mat-icon-button (click)="nextClicked()">
        <mat-icon>keyboard_arrow_right</mat-icon>
      </button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExampleHeader<D> implements OnDestroy {
  private _destroyed = new Subject<void>();
  currentDate: Moment;

  constructor(
    private _calendar: MatCalendar<D>,
    private _dateAdapter: DateAdapter<D>,
    @Inject(MAT_DATE_FORMATS) private _dateFormats: MatDateFormats,
    cdr: ChangeDetectorRef,
  ) {
    _calendar.stateChanges.pipe(takeUntil(this._destroyed)).subscribe(() => cdr.markForCheck());
    this.currentDate = moment();
  }

  ngOnDestroy() {
    this._destroyed.next();
    this._destroyed.complete();
  }

  get periodLabel() {
    return this._calendar.selected ?
      this._dateAdapter.format(this._calendar.selected as D, this._dateFormats.display.dateInput).toLocaleUpperCase()
      : this.currentDate.year()
  }

  previousClicked() {
    this._calendar.activeDate = this._dateAdapter.addCalendarYears(this._calendar.activeDate, -24);
  }

  nextClicked() {
    this._calendar.activeDate = this._dateAdapter.addCalendarYears(this._calendar.activeDate, 24);
  }
}
