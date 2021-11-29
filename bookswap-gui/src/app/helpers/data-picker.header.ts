import {
  Component,
  Inject,
  ChangeDetectionStrategy,
  OnDestroy,
  ChangeDetectorRef
} from '@angular/core';
import {Subject} from "rxjs";
import {takeUntil} from 'rxjs/operators';
import {DateAdapter, MAT_DATE_FORMATS, MatDateFormats} from '@angular/material/core';
import {MatCalendar} from '@angular/material/datepicker';

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
export class DataPickerHeader<D> implements OnDestroy {
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
