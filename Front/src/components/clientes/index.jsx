// components/Personagens.jsx
// import { usePersonagens } from "../../hooks/usePersonagem";
import './index.css'
import { Search, ChevronDown } from "lucide-react";
import { useState } from "react";


function Clientes() {
  // const { clientes, carregando, erro } = useClientes();
  // if (carregando) return <p>Carregando...</p>;
  // if (erro) return <p>Erro: {erro}</p>;

  return (
    <section className="section-clientes">
      <div className="conteudo-95 layout">
        <div className="conteudo">

          <h1 className="titulo t1">Clientes</h1>
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
        </div>
      </div>
    </section>
  );
}

export default Clientes;