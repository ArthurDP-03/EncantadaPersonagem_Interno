import { useState, useEffect } from "react";
import { getAdministrador, deletarAdministrador, criarAdministrador, atualizarAdministrador } from "../services/administradoresService";

export function useAdministradores() {
  const [administradores, setAdministradores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getAdministrador()
      .then(setAdministradores)
      .catch(err => {
        console.error("Erro completo:", err);
        setErro(err.message);
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    await deletarAdministrador(id);
    setAdministradores(prev => prev.filter(a => a.id !== id));
  };

  const criar = async (dados) => {
    const novoAdministrador = await criarAdministrador(dados);
    setAdministradores(prev => [...prev, novoAdministrador]);
  };

  const editar = async (id, dados) => {
    const atualizado = await atualizarAdministrador(id, dados);
    setAdministradores(prev => prev.map(a => a.id === id ? atualizado : a));
  };

  return { administradores, carregando, erro, deletar, criar, editar };
}