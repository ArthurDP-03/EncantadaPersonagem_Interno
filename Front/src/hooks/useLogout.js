import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

function useLogout() {
    const { logout } = useAuth()
    const navigate = useNavigate()

    function handleLogout() {
        logout()
        navigate('/')
    }

    return { handleLogout }
}

export default useLogout