<h1>
<strong>CustomPets</strong>
</h1>

</br>


<p><b> O que é? </b></p>
  <p>Este é um plugin para o jogo Minecraft, onde adiciona mascotes customizados pelo administrador do servidor e pode ser usado pelos jogadores. Estes mascotes seguem o jogador pelo mapa e também podem pegar itens que estiverem perto do mesmo.</p>

</br></br>

<p><b> Dependencias </b></p>

  <ul>
    <li><a href="https://www.spigotmc.org/wiki/buildtools/" target="_blank">Spigot 1.16.4</a></li>
    <li>Bloodmoon (Projeto particular)</li>
    <li>Cash (Projeto particular)</li>
    <li>ItemFilter (Projeto particular)</li>
  </ul>


</br></br>

<h2>
<strong>Conceitos básicos</strong>
</h2>


  <p><b> Suporte de armaduras </b></p>
    <p>Suporte de armaduras são entidades dentro do jogo que servem para segurar e exibir itens vestiveis, como armaduras, cabeças de jogadores, etc.</p>
    </br>

   <img width="250px" height="300px" src="https://i.imgur.com/Qic2HXw.jpg" alt="ArmorStand"> 
    </br>
    <p>Como estas entidades podem equipar cabeças de jogadores e cabeças de jogadores dependem de qual skin o mesmo esta usando, podemos usar inumeras texturas para vestir os suportes de armaduras. Particularmente eu gosto de usar este <a href="https://minecraft-heads.com/custom-heads/head-database" target="_blank">banco de dados.</a></p>
    <p>Suportes de armaduras podem ter 2 tamanhos, normal e pequeno, também podem ser deixadas invisiveis, assim só aparecendo os itens equipados.</p>
    </br>
   
   <img width="700px" height="300px" src="https://i.imgur.com/ua5OnHL.jpg" alt="ArmorStand"> 
   
   
  <p><b> Lobos </b></p>
    <p>Lobos são entidades que podem ser domesticados por jogadores. Quando domesticados seguem os mesmos e os defendem, eles também podem ser deixados invisiveis.</p>
    
   <img width="400px" height="300px" src="https://i.imgur.com/0o9GFRa.jpg" alt="ArmorStand">
   
<h2>
<strong>Conceitos avançados</strong>
</h2>
    <p>Para criarmos um mascote com cabeças customizadas precisamos invocar um lobo fiel ao jogador, armazenar este lobo na memoria viculado ao jogador e teleportar suportes de armaduras invisiveis por onde o lobo andar, porém isso não é o suficiente. Se apenas teleportarmos o suporte de armaduras para a posição do lobo teremos 2 problemas:</p>
    <ul>
      <li>1° -> Elas possuem posições relativas ás coodenadas do lobo, ou seja, todas tem um x, y, z á ser acrescentados de acordo para onde o lobo está olhando;</li>
      <li>2° -> A rotação da cabeça de cada suporte de armaduras é diferente de acordo com a distancia do centro.</li>
    </ul>
    <p>Caso não resolvermos estes 2 problemas o nosso mascote personalizado ficará desta forma:</p>
   
   <img src="https://media.giphy.com/media/ARisZb6AFBq7692aKw/giphy.gif" width="480px" height="270px" frameBorder="0" class="giphy-embed" allowFullScreen>
    
  <p><b> Resolução dos problemas </b></p>
    <p>Precisaremos atualizar cada membro (suporte de armaduras) de acordo com sua posição relativa ao lobo, assim como para onde estarão olhando, pois um membro que esta relativamente na frente de onde o lobo está olhando tem uma rotação diferente do que um membro que esta relativamente atrás de onde o lobo estará olhando.</p>
    
   <p>Descobrir o raio do membro do lobo da forma sqrt(x² + y²), após isso descobrir o real x e y com seno e cosseno do angulo.</p>
   
   <img src="https://i.imgur.com/CMrOsgJ.png" width="400px" height="70px">
   
   <p>Além das coordenadas globais do lobo (x, y, z) nós temos as informações de rotação de cabeça do mesmo, yaw em pitch.</p>
   <img src="https://i.imgur.com/zxnYJVO.jpg" width="480px" height="380px">
   <p>Sabendo disso, podemos usar a informação Yaw para identificar qual direção o lobo esta olhando e transforma-la em graus para descobrir a rotação exata que cada membro deverá fazer.</p>
   
   <img src="https://i.imgur.com/wfJx8D8.jpg" width="400px" height="400px">
   <img src="https://i.imgur.com/ixOb1eh.jpg" width="400px" height="400px">
   
   
   <p>Após a resolução destes 2 problemas chegamos em um resultado perfeito onde cada membro se move relativamente para onde o lobo esta e onde esta olhando.</p>
   <img src="https://media.giphy.com/media/U3GFIhMnsJ3ZLswDTR/giphy.gif" width="480px" height="270px" frameBorder="0" class="giphy-embed" allowFullScreen>
    

