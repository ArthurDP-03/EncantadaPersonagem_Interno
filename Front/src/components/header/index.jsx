import './index.css'

function Header() {
    return (
        <header>
            <section className="cabecalho">
                <div className="conteudo-90">
                    <div className="conteudo">
                        <div className="perfil">
                            <div className="imagem-container">
                                <img src="" alt="" className="foto_perfil" />
                            </div>
                            <div className="textos">
                                <div className="nome">
                                    <p>UserName</p>
                                </div>
                                <div className="cargo">
                                    <p>Administrador</p>
                                </div>
                            </div>
                        </div>
                        <div className="logo">
                            <div className="imagem-container">
                                <img src="src/assets/logo.png" alt="" />
                            </div>
                        </div>
                        <div className="funcionalidades">
                            <div className="imagem-container">
                                <img src="" alt="" />
                            </div>
                            <div className="imagem-container">
                                <img src="" alt="" />
                            </div>
                        </div>
                    </div>
                </div>
            </section>
            <section className='navegacao'>
                <div className='lista-link'>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                </div>
            </section>
        </header>
    )
}

export default Header;