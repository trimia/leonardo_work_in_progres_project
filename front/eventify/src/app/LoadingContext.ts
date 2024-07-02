import React, {createContext} from "react";
import Token from "@/app/Token";



// export const AuthContext = createContext<[Token | null, React.Dispatch<React.SetStateAction<Token|null>>, Function]>([null, ()=>{}, ()=>{}])
export const LoadingContext = createContext<boolean>(true)