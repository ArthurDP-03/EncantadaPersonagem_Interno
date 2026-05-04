import { useState, useEffect } from "react";
import { getAdministrador, deletarAdministrador } from "../services/administradoresService";

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

  return { administradores, carregando, erro, deletar };
}