import React from "react";
import Button from "react-bootstrap/Button";

interface LoginProps {
  usernameSet: (name: string) => void;
}

export function Login(props: LoginProps) {
  let userInput: string = "";

  let onTextInput = (e: React.FormEvent<HTMLInputElement>) => {
    userInput = e.currentTarget.value;
  }

  let onSetUsername = () => {
    props.usernameSet(userInput);
  }

  return (
      <div className="d-flex flex-column min-vh-100 justify-content-center align-items-center">
        <h1>Crazy Eights</h1>
        <p>Enter Username</p>
        <input id="usernameTxt" type="text" onChange={onTextInput}/>
        <Button onClick={onSetUsername} id="startBtn" className="mt-3">Start</Button>
      </div>
  );
}