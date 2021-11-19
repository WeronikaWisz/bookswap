import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../../../services/token-storage.service';
import {Router} from "@angular/router";
import Swal from "sweetalert2";
import {ProfileData} from "../../../models/auth/ProfileData";
import {UsersService} from "../../../services/manage-users/users.service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.sass']
})
export class ProfileComponent implements OnInit {

  isLoggedIn = false;
  profileData?: ProfileData;

  constructor(private tokenStorage: TokenStorageService, private router: Router,
              private usersService: UsersService) { }

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

  }

  changePassword(){

  }

  reloadPage(): void {
    window.location.reload();
  }

}
