import { useState } from 'react'
import axios from 'axios'

const API_URL = 'http://localhost:8082/api/offices/save'

function SaveOffice() {
  const [name, setName] = useState('')
  const [officeNumber, setOfficeNumber] = useState('')
  const [message, setMessage] = useState('')
  const [isError, setIsError] = useState(false)

  async function handleSaveOffice() {
    setMessage('')
    setIsError(false)

    if (!name.trim() || officeNumber === '') {
      setIsError(true)
      setMessage('Please fill office name and office number.')
      return
    }

    try {
      const response = await axios.post(API_URL, {
        name: name.trim(),
        officeNumber: Number(officeNumber),
      })

      const successText = 'Office saved successfully!'
      setIsError(false)
      setMessage(successText)
      alert(successText)
      console.log(response.data)
    } catch (error) {
      const errorText =
        error.response?.data ||
        error.message ||
        'Failed to save office.'
      setIsError(true)
      setMessage(String(errorText))
      alert(String(errorText))
    }
  }

  return (
    <div className="save-office-page">
      <h1>Save office</h1>

      <div className="save-office-row">
        <label>Office Name:</label>
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </div>

      <div className="save-office-row">
        <label>Office Number:</label>
        <input
          type="number"
          value={officeNumber}
          onChange={(e) => setOfficeNumber(e.target.value)}
        />
      </div>

      <button type="button" onClick={handleSaveOffice}>
        SAVE OFFICE
      </button>

      {message ? (
        <p className={isError ? 'save-office-error' : 'save-office-success'}>
          {message}
        </p>
      ) : null}
    </div>
  )
}

export default SaveOffice
