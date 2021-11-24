import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../../../services/token-storage.service';
import {Router} from "@angular/router";
import Swal from "sweetalert2";
import {ProfileData} from "../../../models/manage-users/ProfileData";
import {UsersService} from "../../../services/manage-users/users.service";
import {MatDialog} from "@angular/material/dialog";
import {UpdateProfileDialogComponent} from "./update-profile-dialog/update-profile-dialog.component";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.sass']
})
export class ProfileComponent implements OnInit {

  isLoggedIn = false;
  profileData?: ProfileData;

  constructor(private tokenStorage: TokenStorageService, private router: Router,
              private usersService: UsersService, public dialog: MatDialog) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.getUserProfileData()
    } else {
      this.router.navigate(['/login']).then(() => this.reloadPage());
    }
  }

  getUserProfileData(){
    this.usersService.getUserProfileData().subscribe(
      data => {
        console.log(data)
        this.profileData = data;
      }, err => {
        Swal.fire({
          position: 'top-end',
          title: 'Pobranie danych użytkownika nie powiodło się',
          text: err.error.message,
          icon: 'error',
          showConfirmButton: false
        })
      }
    )
  }

  editProfile(){
    if(this.profileData) {
      const dialogRef = this.dialog.open(UpdateProfileDialogComponent, {
        maxWidth: '650px',
        data: {
          email: this.profileData.email,
          name: this.profileData.name,
          surname: this.profileData.surname,
          phone: this.profileData.phone,
          postalCode: this.profileData.postalCode,
          post: this.profileData.post,
          city: this.profileData.city,
          street: this.profileData.street,
          buildingNumber: this.profileData.buildingNumber,
          doorNumber: this.profileData.doorNumber
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        console.log(result);
        if(result){
          this.getUserProfileData();
        }
      });
    }
  }

  changePassword(){

  }

  reloadPage(): void {
    window.location.reload();
  }

}
