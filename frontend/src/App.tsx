import React, {useState} from 'react';
import './App.css';
import {GameScreen} from "./GameScreen";
import {Login} from "./Login";

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
