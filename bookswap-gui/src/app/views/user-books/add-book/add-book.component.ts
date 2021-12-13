import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, FormGroupDirective, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
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
import {BookData} from "../../../models/user-books/BookData";
import {EBookLabel} from "../../../enums/EBookLabel";
import {TranslateService} from "@ngx-translate/core";

const moment = _rollupMoment || _moment;

export interface Label{
  label: EBookLabel,
  name: string
}

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
  allCategories: string[] = [];
  file: File | null = null;
  fileName: string = '';
  imageUrl = '';

  labels: Label[] = [{label: EBookLabel.PERMANENT_SWAP, name: "Wymiana stała"},
    {label: EBookLabel.TEMPORARY_SWAP, name: "Wymiana tymczasowa"}]

  @ViewChild('categoryInput') categoryInput!: ElementRef<HTMLInputElement>;

  @ViewChild(FormGroupDirective) formGroupDirective!: FormGroupDirective;

  formTitle = "";
  isEditBookView = false;
  bookId?: number;
  checkEditImage = false;

  constructor(private formBuilder: FormBuilder,
              private route: ActivatedRoute, private translate: TranslateService,
              private router: Router, private userBookService : UserBookService,
              private tokenStorage: TokenStorageService) {
    const currentYear = moment();
    this.maxDate = new Date(currentYear.year(), 11, 31);
    this.loadAllCategoryNames();
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
      label: [null, Validators.required],
      description: [''],
      image: ['']
    });
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
    this.checkIfEditBookView();
  }

  loadAllCategoryNames(){
    this.userBookService.loadAllCategoryNames().subscribe(
      data => {
        console.log(data)
        this.allCategories = data;
      }
    )
  }

  checkIfEditBookView(){
    this.route.params
      .subscribe(
        params => {
          console.log(params);
          if (params.id){
            this.isEditBookView = true;
            this.bookId = params.id;
            this.translate.get("user-books.add-book.edit-title").subscribe(data =>
              this.formTitle = data
            );
            this.getBook(params.id)
          } else {
            this.translate.get("user-books.add-book.add-title").subscribe(data =>
              this.formTitle = data
            );
          }
        }
      );
  }

  getBook(id: number) {
    this.userBookService.getBook(this.bookId!).subscribe(
      data => {
        console.log(data)
        this.fillFormWithEditedBook(data);
      }, err => {
        Swal.fire({
          position: 'top-end',
          title: 'Pobranie danych książki nie powiodło się',
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    )
  }

  fillFormWithEditedBook(data: BookData){
    this.form.get('title')?.setValue(data.title);
    this.form.get('author')?.setValue(data.author);
    this.form.get('publisher')?.setValue(data.publisher);
    this.form.get('yearOfPublication')?.setValue(moment('31/12/'+data.yearOfPublication, "DD/MM/YYYY"));
    this.form.get('description')?.setValue(data.description);
    this.form.get('label')?.setValue(this.getLabel(data.label));
    console.log(this.form.get('label')?.value)
    this.categories = data.categories;
  }

  getLabel(label: any): EBookLabel{
    label = label.valueOf() as unknown as string;
    if(label === EBookLabel[EBookLabel.PERMANENT_SWAP]){
      return EBookLabel.PERMANENT_SWAP;
    } else {
      return EBookLabel.TEMPORARY_SWAP;
    }
  }


  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    if (value) {
      if(!this.categories.find(c => c === value)) {
        this.categories.push(value);
        if(this.form.pristine){
          this.form.markAsDirty();
        }
      }
    }

    event.chipInput!.clear();

    this.categoriesCtrl.setValue(null);
  }

  remove(category: string): void {
    const index = this.categories.indexOf(category);

    if (index >= 0) {
      this.categories.splice(index, 1);
      if(this.form.pristine){
        this.form.markAsDirty();
      }
    }
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    if(!this.categories.find(c => c === event.option.viewValue)) {
      this.categories.push(event.option.viewValue);
      if(this.form.pristine){
        this.form.markAsDirty();
      }
    }
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
        this.clearFields();
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

  updateBook(){
    this.userBookService.updateBook({
      "title": this.form.get('title')?.value,
      "author": this.form.get('author')?.value,
      "publisher": this.form.get('publisher')?.value,
      "yearOfPublication": this.form.get('yearOfPublication')?.value.year(),
      "description": this.form.get('description')?.value,
      "categories": this.categories,
      "label": this.form.get('label')?.value
    }, this.file, this.bookId!).subscribe(
      data => {
        console.log(data);
        this.form.markAsPristine();
        Swal.fire({
          position: 'top-end',
          title: 'Pomyśnie zaktualizowano książkę',
          icon: 'success',
          showConfirmButton: false
        })
      },
      err => {
        Swal.fire({
          position: 'top-end',
          title: 'Aktualizacja nie powiodła się',
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    );
  }

  clearFields(){
    console.log("cleaned")
    this.file = null;
    this.fileName = '';
    this.categories = [];
    this.clearFormGroupDirective();
  }

  clearFormGroupDirective() {
    this.formGroupDirective.resetForm();
  }

  onFileSelected(event: any) {
    this.file = event.target.files[0];
    if (this.file) {
      this.fileName = this.file.name;
    } else {
      this.fileName = '';
    }
  }

  reloadPage(): void {
    window.location.reload();
  }

}
