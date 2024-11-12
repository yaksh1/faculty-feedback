

// export default Hero
import React, { useState } from 'react'
import { Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'
import Paper from '@mui/material/Paper'
import './Hero.css';


const Hero = ({ faculties =[]}) => {
  const [searchQuery, setSearchQuery] = useState('')
    const navigate = useNavigate()
    

  // Filter faculties based on search query
  const filteredFaculties = faculties.filter(faculty =>
    faculty.name.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const handleSearch = e => {
    setSearchQuery(e.target.value)
  }

  const handleReviewClick = facultyId => {
    navigate(`/Reviews/${facultyId}`)
  }

  return (
    <div className='home-page-container'>
      <div className='search-bar-container'>
        <input
          type='text'
          placeholder='Search faculty by name...'
          value={searchQuery}
          onChange={handleSearch}
          className='search-bar'
        />
      </div>

      <div className='faculty-grid'>
        {filteredFaculties.map(faculty => (
          <Paper key={faculty.facultyId} className='faculty-card'>
  <div className='faculty-card-content'>
    <div className='faculty-image-container'>
      <img src={faculty.image} alt={faculty.name} className='faculty-image' />
    </div>
    <div className='faculty-info'>
      <h4 className='faculty-name'>{faculty.name}</h4>
      <div className='review-button-around'>
        <Button
          variant='info'
          onClick={() => handleReviewClick(faculty.facultyId)}
          className='faculty-review-button'
        >
          Reviews
        </Button>
      </div>
    </div>
  </div>
</Paper>

        ))}
      </div>
    </div>
  )
}

export default Hero
