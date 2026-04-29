// components/Personagens.jsx
import { usePersonagens } from "../../hooks/usePersonagem";
import './index.css'
import { Search, ChevronDown } from "lucide-react";
import logo from '../../assets/logo.png'
import { useState } from "react";


function Personagens() {
  const { personagens, carregando, erro } = usePersonagens();
  const [imagem, setImagem] = useState(logo);
  if (carregando) return <p>Carregando...</p>;
  if (erro) return <p>Erro: {erro}</p>;

  return (
    <section className="section-personagens">
      <div className="conteudo-95 layout">
        <div className="conteudo">

          <h1 className="titulo t1">Personagens</h1>
          <div className="filtros">
            <div className="input-container">
              <select className="input">
                <option value="">Ordenar</option>
                <option value="">Alfabética(A-Z)</option>
                <option value="">Alfabética(Z-A)</option>
                <option value="">Disponibilidade</option>

              </select>
              <ChevronDown className="icon" size={18} />
            </div>
            <div className="input-container">
              <input type="text" placeholder="Buscar" className="input" />
              <Search className="icon" size={18} />
            </div>
          </div>
          <ul className="lista-personagem">
            {personagens.map(personagem => (
              <li key={personagem.id} className="card-personagem">
                <div className="imagem-container">
                  <img src={imagem} alt="" className="imagem" onLoad={() => { if (personagem.foto) { setImagem(personagem.foto); } }} onError={() => { setImagem(logo); }}/>                
                </div>
                <div className="textos">
                  <div className="texto t1"><p>{personagem.nome}</p></div>
                  <div className="texto t2"><p>Disponibilidade: 8/10</p></div>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </section>
  );
}

export default Personagens;