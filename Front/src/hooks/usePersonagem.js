import { useState, useEffect } from "react";
import { getPersonagens } from "../services/personagensService";

export function usePersonagens() {
  const [personagens, setPersonagens] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getPersonagens()
        .then(setPersonagens)
        .catch(err => {
            console.error("Erro completo:", err);
            setErro(err.message);
        })
        .finally(() => setCarregando(false));
  }, []);

  return { personagens, carregando, erro };
}