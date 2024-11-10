import { useEffect, useRef } from 'react'
import api from '../../api/axiosConfig'
import { useParams } from 'react-router-dom'
import { Container, Row, Col } from 'react-bootstrap'
import React from 'react'
import './Reviews.css' // Make sure to include your CSS here

const Reviews = ({ getMovieData, faculty, reviews, setReviews }) => {
 
  const revText = useRef()
  let params = useParams()
  const facultyId = params.facultyId

  useEffect(() => {
    getMovieData(facultyId)
  }, [facultyId])


  const addReview = async e => {
    e.preventDefault()
    const rev = revText.current.value

    if (!rev) return // Prevent empty submissions

    try {
      const token = localStorage.getItem('token')
     const response = await fetch(
  `http://localhost:8080/user/reviews?reviewBody=${encodeURIComponent(
    rev
  )}&facultyId=${encodeURIComponent(facultyId)}`,
  {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}` // Include the Authorization header
    }
  }
)

if (!response.ok) {
  throw new Error('Network response was not ok')
}

      
await getMovieData(facultyId)      
revText.current.value=''
// const data = await response.json()
   

// const updatedReviews = [...reviews, data.data.body]

// revText.current.value = '' // Clear the input after submission

// setReviews(updatedReviews) // Update the state with the new reviews

    } catch (err) {
      console.error(err)
    }
}


  return (
    <Container fluid className='reviews-container'>
      <Row>
        <Col xs={12} md={6} className='faculty-image-col'>
          <img src={faculty?.image} alt='Faculty' className='faculty-image' />
        </Col>
        <Col xs={12} md={6} className='form-and-reviews-col'>
          <form className='review-form' onSubmit={addReview}>
            <textarea
              ref={revText}
              className='review-textarea'
              placeholder='Write your review here...'
            />
            <button type='submit' className='submit-button'>
              Submit Review
            </button>
          </form>
          <div className='reviews-list'>
            {reviews.map((r, index) => (
              <div key={index} className='single-review'>
                {r.body}
              </div>
            ))}
          </div>
        </Col>
      </Row>
    </Container>
  )
}

export default Reviews
