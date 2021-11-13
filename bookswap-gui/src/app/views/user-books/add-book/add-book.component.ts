import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../../services/auth.service";
import {TokenStorageService} from "../../../services/token-storage.service";
import Swal from "sweetalert2";
import {DataPickerHeader, MY_FORMATS} from "../../../helpers/data-picker.header";
import {MatDatepicker} from "@angular/material/datepicker";

import * as _moment from 'moment';
import {default as _rollupMoment, Moment} from 'moment';
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from "@angular/material/core";
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter} from "@angular/material-moment-adapter";
import {Observable} from "rxjs";
import {map, startWith} from "rxjs/operators";
import {MatChipInputEvent} from "@angular/material/chips";
import {MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {UserBookService} from "../../../services/user-book.service";

const moment = _rollupMoment || _moment;

@Component({
  selector: 'app-add-book',
  templateUrl: './add-book.component.html',
  styleUrls: ['./add-book.component.sass'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS],
    },

    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
  ]
})
export class AddBookComponent implements OnInit {

  form!: FormGroup;
  isLoggedIn = false;
  roles: string[] = [];
  maxDate: Date;
  dataPickerHeader = DataPickerHeader;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  categoriesCtrl = new FormControl();
  filteredCategories: Observable<string[]>;
  categories: string[] = [];
  allCategories: string[] = ['przygodowa', 'obyczajowa'];
  file: File | null = null;
  fileName: string = '';
  imageUrl = '';

  @ViewChild('categoryInput') categoryInput!: ElementRef<HTMLInputElement>;

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute,
              private router: Router, private userBookService : UserBookService,
              private authService: AuthService, private tokenStorage: TokenStorageService) {
    const currentYear = moment();
    this.maxDate = new Date(currentYear.year(), 11, 31);
    this.filteredCategories = this.categoriesCtrl.valueChanges.pipe(
      startWith(null),
      map((category: string | null) => (category ? this._filter(category) : this.allCategories.slice())),
    );
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      title: ['', Validators.required],
      author: ['', Validators.required],
      publisher: ['', Validators.required],
      yearOfPublication: ['', Validators.required],
      label: ['PERMANENT_SWAP'],
      description: ['']
    });
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
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

  chosenYearHandler(normalizedYear: Moment, datepicker: MatDatepicker<Moment>) {
    this.form.get('yearOfPublication')!.setValue(moment());
    const ctrlValue = this.form.get('yearOfPublication')!.value;
    ctrlValue.year(normalizedYear.year());
    this.form.get('yearOfPublication')!.setValue(ctrlValue);
    datepicker.close();
  }

  onSubmit(): void {
    this.userBookService.addBook({
      "title": this.form.get('title')?.value,
      "author": this.form.get('author')?.value,
      "publisher": this.form.get('publisher')?.value,
      "yearOfPublication": this.form.get('yearOfPublication')?.value.year(),
      "description": this.form.get('description')?.value,
      "categories": this.categories,
      "label": this.form.get('label')?.value
    }, this.file).subscribe(
      data => {
        console.log(data);
        Swal.fire({
          position: 'top-end',
          title: 'Pomyśnie dodano książkę',
          icon: 'success',
          showConfirmButton: false
        })
      },
      err => {
        Swal.fire({
          position: 'top-end',
          title: 'Dodawanie nie powiodło się',
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    );
  }

  onFileSelected(event: any) {
    this.file = event.target.files[0];
    if (this.file) {
      this.fileName = this.file.name;
      // var reader = new FileReader();
      // reader.readAsDataURL(event.target.files[0]); // read file as data url
      // reader.onload = (event) => { // called once readAsDataURL is completed
      //   this.imageUrl = event.target!.result as string;
      // }
    } else {
      this.fileName = '';
      // this.imageUrl = '';
    }
  }

  reloadPage(): void {
    window.location.reload();
  }

}
