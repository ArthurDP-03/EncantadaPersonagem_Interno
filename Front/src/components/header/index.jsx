import './index.css'

function Header() {
    return (
        <header>
            <section className="cabecalho">
                <div className="conteudo-90">
                    <div className="perfil">
                        <div className="imagem-container">
                            <img src="src/assets/logo.png" alt="" className="foto_perfil" />
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
                    <div className="logo"></div>
                    <div className="funcionalidades">
                        <div className="imagem-container">
                            <img src="" alt="" />
                        </div>
                        <div className="imagem-container">
                            <img src="" alt="" />
                        </div>
                    </div>
                </div>
            </section>
        </header>
    )
}

export default Header;