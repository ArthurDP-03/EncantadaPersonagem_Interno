import { useState, useEffect } from "react";
import { getAtores, deletarAtor, criarAtor } from "../services/atoresService";

export function useAtores() {
  const [atores, setAtores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getAtores()
      .then(setAtores)
      .catch(err => {
        console.error("Erro completo:", err);
        setErro(err.message);
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    await deletarAtor(id);
    setAtores(prev => prev.filter(a => a.id !== id));
  };
  
  const criar = async (dados) => {
    const novoAtor = await criarAtor(dados);
    setAtores(prev => [ ...prev, novoAtor]);
  };
  

  return { atores, carregando, erro, deletar, criar };
}