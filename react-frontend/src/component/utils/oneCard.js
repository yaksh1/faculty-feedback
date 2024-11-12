import Card from 'react-bootstrap/Card'
import Button from 'react-bootstrap/Button'


function OneCard (faculty) {
  return (
  <Card style={{ width: '18rem' }}>
  <Card.Img variant='top' src={faculty.image} />
  <Card.Body>
        <Card.Title>{ faculty.name}</Card.Title>
    <Card.Text>
          { faculty.position}
    </Card.Text>
    <Button variant='primary'>Reviews</Button>
  </Card.Body>
</Card>

  )
}

export default OneCard
