import {Col, Container, Row} from "react-bootstrap";
import Button from "react-bootstrap/Button";

export enum Suit {
  CLUBS,
  DIAMONDS,
  HEART,
  SPADES,
}

export enum Rank {
  TWO,
  THREE,
  FOUR,
  FIVE,
  SIX,
  SEVEN,
  EIGHT,
  NINE,
  TEN,
  JACK,
  QUEEN,
  KING,
  ACE
}

export interface Card {
  suit: Suit;
  rank: Rank;
}

function toText(c: Card): string {
  let s: string = "";
  let r: string = "";

  switch (c.rank) {
    case Rank.ACE: {
      r = "1";
      break;
    }
    case Rank.TWO: {
      r = "2";
      break;
    }
    case Rank.THREE: {
      r = "3";
      break;
    }
    case Rank.FOUR: {
      r = "4";
      break;
    }
    case Rank.FIVE: {
      r = "5";
      break;
    }
    case Rank.SIX: {
      r = "6";
      break;
    }
    case Rank.SEVEN: {
      r = "7";
      break;
    }
    case Rank.EIGHT: {
      r = "8";
      break;
    }
    case Rank.NINE: {
      r = "9";
      break;
    }
    case Rank.TEN: {
      r = "10";
      break;
    }
    case Rank.JACK: {
      r = "J";
      break;
    }
    case Rank.QUEEN: {
      r = "Q";
      break;
    }
    case Rank.KING: {
      r = "K";
      break;
    }
  }

  switch (c.suit) {
    case Suit.CLUBS:
      s = "C";
      break;
    case Suit.DIAMONDS:
      s = "D";
      break;
    case Suit.HEART:
      s = "H";
      break;
    case Suit.SPADES:
      s = "S";
      break;
  }

  return r + s;
}

function PlayerHand(props: { hand: Card[], myTurn: boolean }) {
  return (
      <Container>
        <Row>
          <h6>Your Hand</h6>
        </Row>
        <Row>
          {
            props.hand.map(c => toText(c))
                .map(c =>
                    <Col>
                      <Button disabled={!props.myTurn} className="btn">
                        {c}
                      </Button>
                    </Col>
                )
          }
        </Row>
        <Row>
        </Row>
      </Container>
  );
}

export default PlayerHand;