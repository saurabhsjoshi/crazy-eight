import React, {useState} from 'react';
import './App.css';
import Button from 'react-bootstrap/Button';
import {Container, Row} from "react-bootstrap";

interface LoginProps {
  usernameSet: (name: string) => void;
}

function Login(props: LoginProps) {
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

function GameScreen(props: { username: string }) {
  return (
      <Container className="max-width">
        <Row>
          <h1>Crazy Eights</h1>
        </Row>
        <Row>
          <h6 id="usernameLbl">Username: {props.username}</h6>
        </Row>
      </Container>
  )
}

function App() {
  const [username, setUsername] = useState<string | null>(null);

  return (
      <>
        {(username == null) ?
            <Login usernameSet={(name: string) => setUsername(name)}/> :
            <GameScreen username={username}/>
        }
      </>
  );
}

export default App;
