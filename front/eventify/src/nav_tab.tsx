"use client"
import { Button} from "@mantine/core";
import { IconPhoto, IconMessageCircle, IconSettings } from "@tabler/icons-react";

export default function NavTab(props: any){
    return(
    <div className="nav">
        <div className={props.tabs == "settings" ? "nav_active" : "nav_not_active"} 
            onClick={() => props.setTabs("settings")}>home</div>
        <div className={props.tabs == "user" ? "nav_active" : "nav_not_active"} 
            onClick={() => props.setTabs("user")}>user</div>
        <div className={props.tabs == "eventi" ? "nav_active" : "nav_not_active"} 
            onClick={() => props.setTabs("user")}>i miei eventi</div>
        <div className={props.tabs == "logout" ? "nav_active" : "nav_not_active"} 
            onClick={() => props.setTabs("ciao")}>logout</div>
    </div>
    )
}