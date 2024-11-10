import React, { useState } from 'react'
// import '../signup_login/AuthPage.css' 
import './VerifyCodePage.css'
import '../utils/modal.css'
import tickAnimation from '../../assets/animations/tick.lottie'
import errorAnimation from '../../assets/animations/error.lottie'
import verifyAnimation from '../../assets/animations/verify.lottie'
import { DotLottieReact } from '@lottiefiles/dotlottie-react'



function VerifyCodePage ({ email, onVerified }) {
  const [otp, setOtp] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const [showModal, setShowModal] = useState(false) // State to control modal visibility
  const [showErrorModal, setShowErrorModal] = useState(false); // State for error modal
  const [errorMessage, setErrorMessage] = useState(''); // State to hold error message

  const handleSubmit = async e => {
    e.preventDefault()
    setLoading(true)

    try {
      const response = await fetch('http://localhost:8080/auth/verify', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, otp })
      })

      const data = await response.json()

      if (response.ok) {
        
        setShowModal(true) // Show modal on successful verification
      } else {
         if (data.errorCode === 'OTP_EXPIRED') {
          setErrorMessage("OTP already expired, please request for a new OTP."); // Set the error message
          setShowErrorModal(true); // Show the error modal
         }else if (data.errorCode === 'INVALID_OTP') {
          setErrorMessage("Invalid OTP, please try again."); // Set the error message
          setShowErrorModal(true); // Show the error modal
        }
         else {
        setMessage(data.message || 'Invalid OTP. Please try again.')
      }
      }
    } catch (error) {
      setMessage('An error occurred. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleBackToLogin = () => {
  setShowModal(false)
  onVerified() // Call the function to go back to the login page
}


   return (
     <div className='container'>
      <div className='form-container'>
       <div className='my-verify-lottie-player-container'>
              <DotLottieReact src={verifyAnimation} loop autoplay/>
        </div>
        <form onSubmit={handleSubmit}>
          <h1>Enter Verification Code</h1>
          <p>Enter the 6-digit code sent to your email</p>
          <input
            type='text'
            placeholder='6-digit code'
            value={otp}
            onChange={e => setOtp(e.target.value)}
            maxLength={6}
            required
          />
          <button type='submit' disabled={loading}>
            {loading ? <span className='spinner'></span> : 'Verify'}
          </button>
          {message && <p>{message}</p>}
        </form>
      </div>

      {/* Modal for verification success */}
      {showModal && (
        <div className='modal'>
           <div className='modal-content'>
            <div className='my-lottie-player-container'>
              <DotLottieReact src={tickAnimation} loop autoplay/>
            </div>
             <h4>Account Verified Successfully!</h4>
            <button onClick={handleBackToLogin}>Back to Login</button>
          </div>
        </div>
       )}
        {/* Modal for error handling */}
      {showErrorModal && (
        <div className='modal'>
          <div className='modal-content'>
            <div className='my-lottie-player-container'>
              <DotLottieReact src={errorAnimation} loop autoplay/>
            </div>
            <h2>Error</h2>
            <p>{errorMessage}</p>
            <button onClick={() => setShowErrorModal(false)}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default VerifyCodePage;