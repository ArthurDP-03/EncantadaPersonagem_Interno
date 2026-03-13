import './index.css'

function Login() {
    return (
        <section className="section-login">
            <div className='form-container'>
                <div className="imagem-container">
                    <img src="src/assets/logo.png" alt="Logo" className='imagem'></img>
                </div>
                <h1 className='title'>Login</h1>
                <form action="" className='form'>
                    <div className='input-container text'>
                        <label htmlFor="user" className='label'>Usuário</label>
                        <input type="text" id="user" placeholder="" className='input'></input>
                    </div>
                    <div className='input-container text'>
                        <label htmlFor="password" className='label'>Senha</label>
                        <input type="password" id="password" placeholder="" className='input'></input>
                    </div>
                    <div className='input-container checkbox'>
                        <input type="checkbox" id="remember"/>
                        <label htmlFor="remember" className='label'>Manter login</label>
                    </div>
                    <button type="submit" className='button'>Entrar</button>
                </form>
            </div>
        </section>
    )
}

export default Login;