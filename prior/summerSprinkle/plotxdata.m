function [] = plotxdata(xdata,x,y,N)
% Setup the figure.
clf reset
set(gcf,'NextPlot','replace','Color','black','Renderer','opengl','Position',[10,50,1900,950])
colormap(jet) %color

% Setup the axes.
axis vis3d ij off
set(gca,'XLim',[-250 250],'YLim',[-250 250],'ZLim',[-20 10])
% view(56,26);camzoom(2);
view(-90,30);camzoom(25);

% Create the patch.
p = patch( ...
    'Marker','.','MarkerSize',.05, ...
    'EdgeColor','none','FaceColor','none',...
    'MarkerEdgeColor','flat');

for i=1:N
    str=sprintf('Viewing frame %d/%d',i,N);
    set(gcf,'Name',str);
    
    Y = xdata(x(i):y(i),1); %swap axis to correct image
    X = xdata(x(i):y(i),2); %swap axis to correct image
    Z = xdata(x(i):y(i),3);
    C = xdata(x(i):y(i),4);      
    set(p,'XData',X,'YData',Y,'ZData',Z,'CData',C)
    drawnow     
end

